import re
from VacuumCleaner import VacuumCleaner


class StoryProcessor(object):
    def __init__(self, topics):
        self.topics = topics

    def story_generator(self, data):
        word_list = []
        dataset_type = None
        doc_id = None

        for word in data.split():

            word_list.append(word)

            # detect data set
            if "LEWISSPLIT" in word:
                if "TRAIN" in word:
                    dataset_type = 1
                elif "TEST" in word:
                    dataset_type = 2
                else:
                    continue

            # detect id
            if "NEWID" in word:
                doc_id = re.findall(r"\d+", word)
                doc_id = int(doc_id[0])

            # detect end of a story
            if word == "</REUTERS>":
                if dataset_type:
                    story = " ".join(word_list)
                    result = self.process_story(story)
                    if result:
                        tag, tokens = result
                        yield tag, dataset_type, doc_id, tokens

                # get ready for next stories
                word_list = []
                dataset_type = None
                doc_id = None

    def process_story(self, story):
        story_topics = TopicManager.extract_topics(story)
        tag = TopicManager.check_topics(story_topics, self.topics)
        if tag:  # if the doc belongs to one and only one topic
            tokens = self.get_tokens(story)
            return tag, tokens

    def get_tokens(self, story):
        news_text = self.extract_news_text(story)
        tokens = VacuumCleaner.clean(news_text)
        return tokens

    @staticmethod
    def extract_news_text(story):
        news_text = ""

        title = re.findall("<TITLE>.+?<\/TITLE>", story)
        if title:
            title = str(title)
            title = title.replace("<TITLE>", "")
            title = title.replace("</TITLE>", "")
            news_text += title

        body = re.findall("<BODY>.+?<\/BODY>", story)
        if body:
            body = str(body)
            body = body.replace("<BODY>", "")
            body = body.replace("</BODY>", "")
            news_text += body

        return news_text


class TopicManager(object):
    @staticmethod
    def check_topics(story_topics, tags):
        story_tag = ""
        count = 0
        for topic in story_topics:
            if topic in tags:
                count += 1
                story_tag = topic

        if count == 1:
            return story_tag
        else:
            return False

    @staticmethod
    def extract_topics(story):
        raw_topics = re.findall("<TOPICS>.+?<\/TOPICS>", story)
        topics = re.findall("<D>.+?<\/D>", str(raw_topics))
        for i, topic in enumerate(topics):
            topic = topic.replace("<D>", "")
            topic = topic.replace("</D>", "")
            topics[i] = topic
        return topics
