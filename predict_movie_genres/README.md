# predict the genres of a movie, given its synopsis.

## Introduction

expose a Flask REST API according to the REST API specification in `api.yml` 

you can view this specification at [editor.swagger.io](https://editor.swagger.io)

1. A training endpoint at `localhost:5000/genres/train` to which you can POST a CSV with header `movie_id,synopsis,genres`, where `genres` is a space-separated list of movie genres.
2. A prediction endpoint at `localhost:5000/genres/predict` to which you can POST a CSV with header `movie_id,synopsis` and returns a CSV with header `movie_id,predicted_genres`, where `predicted_genres` is a space-separated list of the top 5 movie genres.

## Getting started

### Local setup

1. Install [Miniconda](https://conda.io/miniconda.html) if you don't have it already.
2. Run `conda env create` from the repo's base directory to create the repo's conda environment from `environment.yml`.
3. Run `conda activate challenge-env` to activate the conda environment.

## Running and evaluating your solution

Every time you push your code to GitLab your solution will automatically be run and evaluated in a GitLab Pipeline:

1. First, `train_predict.sh` will:
    1. Create and activate the Conda environment.
    2. Download the dataset from Kaggle.
    3. Start your Flask API as `env FLASK_APP=challenge flask run`
    4. POST `train.csv` to `localhost:5000/genres/train` to train your model.
    5. POST `test.csv` to `localhost:5000/genres/predict` to create a `submission.csv` with the top 5 predicted genres for each test movie.
    6. Upload `submission.csv` to the [Kaggle competition](https://www.kaggle.com/t/13e0d7502d7746459ef8e4c594a6219a) (on GitLab only).
2. Then, `score.sh` will:
    1. Get your submission's Kaggle score, which is the [Mean Average Precision at K](https://en.wikipedia.org/wiki/Evaluation_measures_%28information_retrieval%29) of the top 5 predicted genres.
    2. Compute a score that indicates the quality of your code.
    3. Print your final score, which is the geometric mean of (1) and (2).

You can run these scripts locally as much as you want using `./train_predict.sh` and `./score.sh`. 
