source ibmcloud/test_credentials.py
pip install twine
python setup.py sdist bdist_wheel
twine check dist/*
twine upload -u $pypi_user -p $pypi_password dist/*