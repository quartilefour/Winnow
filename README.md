# GFN (Winnow)
Gene Function Navigation Tool

## Project Structure

### Docker Setup
 * Dockerfile
 * docker-compose.yml
 
## Code Organization
* django/ - Django backend
* ingest/ - Data ingest and compute
* winnow-ui/ - ReactJS front end

### Django Application
 ```
django
├── gfn (django project settings)
├── manage.py
├── requirements.txt
└── winnow (django backend application)
    ├── admin.py
    ├── apps.py
    ├── migrations (database migrations)
    ├── models.py  (data models)
    ├── tests      (test suite)
    └── views.py
```
#### Python Virtual Environment 
For local development, non-Docker
```shell script
python -m venv gfn_venv
```
#### Python Requirements
```shell script
pip install -r requirements.txt
```


### ReactJS UI
```
winnow-ui
├── package.json
├── package-lock.json
├── public
└── src
```