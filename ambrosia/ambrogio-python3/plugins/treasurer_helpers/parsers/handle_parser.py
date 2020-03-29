from plugins.treasurer_helpers.custom_exceptions import InvalidHandle
from plugins.treasurer_helpers.validator import Validator


class HandleParser:
    @staticmethod
    def replace_group_handle_with_people_handles(
        debtor_strings: list, groups: dict
    ) -> list:
        """
        :param debtor_strings: ["MOVIEBUFF", "FC"]
        :param groups: {"MOVIEBUFF": {"LQ","KR"} }
        :return: handles ["LQ", "KR", "FC"]
        """
        group_names = set(groups.keys())
        handles = list()
        for debtor_string in debtor_strings:
            if debtor_string in group_names:
                people = groups.get(debtor_string, set())
                for person in people:
                    handles.append(person)
            elif Validator.is_valid_person_handle(debtor_string):
                handles.append(debtor_string)
            else:
                raise InvalidHandle("neither group_name nor valid person")
        return handles
