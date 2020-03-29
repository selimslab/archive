#!/usr/bin/env bash
set -e

# Please do not edit this file!

TRAIN_CSV=train.csv
TEST_CSV=test.csv
SUBMISSION_CSV=submission.csv

function log {
    local PURPLE='\033[0;35m'
    local NOCOLOR='\033[m'
    local BOLD='\033[1m'
    local NOBOLD='\033[0m'
    echo -e -n "${PURPLE}${BOLD}$1${NOBOLD}${NOCOLOR}"
}

function create_conda_environment {
    log "Creating the Conda environment if it doesn't exist... "
    conda env create > /dev/null 2>&1 || true
    log "Done!\\n"
}

function activate_conda_environment {
    local CHALLENGE_ENV=challenge-env
    log "Activating the Conda environment... "
    if [[ $CONDA_DEFAULT_ENV != "$CHALLENGE_ENV" ]]
    then
        # shellcheck disable=SC1091
        source activate $CHALLENGE_ENV
    fi
    log "Done!\\n"
}

function set_kaggle_credentials {
    if [[ "$CI_SERVER_NAME" == "GitLab" ]]
    then
        if [[ -z $KAGGLE_USERNAME ]] || [[ -z $KAGGLE_KEY ]]
        then
            log "Please set KAGGLE_USERNAME and KAGGLE_KEY in Settings > CI / CD > Variables to continue!\\n"
            exit 1
        fi
    else
        if [[ ! -f "$HOME/.kaggle/kaggle.json" ]]
        then
            if [[ ! -f "./kaggle.json" ]]
            then
                log "Please download kaggle.json and place it in this directory to continue!\\n"
                exit 1
            else
                export KAGGLE_CONFIG_DIR=.
            fi
        else
            export KAGGLE_CONFIG_DIR=$HOME/.kaggle
        fi
        chmod 600 "$KAGGLE_CONFIG_DIR"/kaggle.json
    fi
}

function download_dataset_from_kaggle {
    local TRAIN_ZIP=${TRAIN_CSV}.zip
    local TEST_ZIP=${TEST_CSV}.zip
    if [[ ! -f $TRAIN_CSV ]] || [[ ! -f $TEST_CSV ]]
    then
        if [[ ! -f $TRAIN_ZIP ]] || [[ ! -f $TEST_ZIP ]]
        then
            log "Downloading the challenge dataset from Kaggle... "
            kaggle competitions download --competition radixai-challenge > /dev/null 2>&1
            log "Done!\\n"
        fi
        log "Unzipping the challenge dataset... "
        unzip -q $TRAIN_ZIP && rm $TRAIN_ZIP && chmod 644 $TRAIN_CSV
        unzip -q $TEST_ZIP && rm $TEST_ZIP && chmod 644 $TEST_CSV
        log "Done!\\n"
    fi
}

function start_flask_api {
    log "Starting your Flask API server in the background... \\n\\n"
    env FLASK_APP=challenge flask run &
    FLASK_API_PID=$!
    # shellcheck disable=SC2034  # Wait until the server is running.
    for i in {1..30}
    do
        if lsof -i:5000 -t > /dev/null;
        then
            break
        else
            sleep 1
        fi
    done
    log "\\n"
}

function train_model {
    log "Training: POST $TRAIN_CSV to localhost:5000/genres/train... \\n\\n"
    curl \
        --silent \
        --fail \
        --data-binary @$TRAIN_CSV \
        --header "Content-Type: text/csv" \
        localhost:5000/genres/train
    log "\\n"
}

function compute_predictions {
    log "\\nPredicting: POST $TEST_CSV to localhost:5000/genres/predict > submission.csv... \\n\\n"
    curl \
        --silent \
        --fail \
        --data-binary @$TEST_CSV \
        --header "Content-Type: text/csv" \
        --header "Accept: text/csv" \
        localhost:5000/genres/predict \
        > $SUBMISSION_CSV
    log "\\n"
}

function stop_flask_api {
    log "Shutting down the Flask server... "
    kill $FLASK_API_PID
    log "Done!\\n"
}
trap stop_flask_api SIGINT
trap stop_flask_api SIGTERM

function submit_predictions_to_kaggle {
    if [[ "$CI_SERVER_NAME" == "GitLab" ]]
    then
        log "Submitting the predictions $SUBMISSION_CSV to Kaggle... "
        kaggle competitions submit \
            --competition radixai-challenge \
            --file $SUBMISSION_CSV \
            --message "GitLab submission" > /dev/null 2>&1
        log "Done!\\n"
    else 
        log "The script was run locally, not submitting to Kaggle.\\n"
    fi
}

create_conda_environment
activate_conda_environment
set_kaggle_credentials
download_dataset_from_kaggle
start_flask_api
train_model || true
compute_predictions || true
stop_flask_api
submit_predictions_to_kaggle
