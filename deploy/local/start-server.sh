#!/usr/bin/env bash
# the scripts makes sure the appropriate tbales 

python manage.py migrate

python manage.py runserver 0.0.0.0:8000