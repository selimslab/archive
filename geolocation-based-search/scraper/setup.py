# Automatically created by: shub deploy

from setuptools import setup, find_packages

setup(
    name="app",
    version="1.0",
    packages=find_packages(),
    package_data={"app": ["*.txt"]},
    entry_points={"scrapy": ["settings = app.settings"]},
)
