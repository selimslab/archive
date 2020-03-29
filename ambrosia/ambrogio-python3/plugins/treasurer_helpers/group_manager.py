"""
a helper class to manage groups
"""
from plugins.treasurer_helpers.validator import Validator
from plugins.treasurer_helpers.custom_exceptions import InvalidHandle, GroupException


class GroupManager:
    """
    a mixin for Treasurer, to handle group commands
    """

    @staticmethod
    def create_group(groups, message):
        try:
            command, group_name = message.text.split()
        except ValueError:
            raise

        if not Validator.is_valid_group_name(group_name):
            raise GroupException("invalid group name")

        if group_name in groups:
            raise GroupException("group already created")

        groups[group_name] = set()

        return groups

    @staticmethod
    def get_group_name_and_handle(groups: dict, message):
        try:
            command, person_handle, group_name = message.text.split()
        except ValueError:
            raise

        if group_name not in groups:
            raise GroupException("group_name not in groups")

        if not Validator.is_valid_person_handle(person_handle):
            raise InvalidHandle("invalid handle")

        return group_name, person_handle

    @classmethod
    def delete_from_group(cls, groups: dict, message):
        try:
            group_name, handle = cls.get_group_name_and_handle(groups, message)
        except ValueError:
            raise

        members: set = groups.get(group_name, set())
        if not members:
            raise GroupException("empty group")
        if handle not in members:
            raise GroupException("member not in the group")

        remaining_members = {member for member in members if member != handle}
        groups[group_name] = remaining_members

        return groups

    @classmethod
    def add_to_group(cls, groups, message):
        try:
            group_name, handle = cls.get_group_name_and_handle(groups, message)
        except ValueError:
            raise

        members = groups.get(group_name, set())
        if handle in members:
            raise GroupException("already in the group")

        members.add(handle)
        groups[group_name] = members
        return groups
