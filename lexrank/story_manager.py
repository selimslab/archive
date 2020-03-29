from file_manager import FileManager


class StoryManager(object):
    def __init__(self):
        pass

    @classmethod
    def get_story_and_summary(cls, filepath):
        filecontent = FileManager.read_lines(filepath)
        story, summary = cls.process_file(filecontent)
        return story, summary

    @staticmethod
    def process_file(filecontent):
        story = []
        summary = []
        is_story = True
        is_summary = False

        for line in filecontent:
            if line == "\n":  # story end
                is_story = False
                is_summary = True
                continue

            if is_story:
                story.append(line)

            elif is_summary:
                summary.append(line)

        return story, summary
