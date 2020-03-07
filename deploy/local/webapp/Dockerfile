FROM python:3.6-alpine
RUN apk update && apk add postgresql-dev gcc python3-dev musl-dev
RUN apk add --no-cache bash
COPY /deploy/local/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
RUN mkdir /code
WORKDIR /code
COPY /code/django/requirements.txt /code/
COPY /deploy/local/start-server.sh /code/
RUN chmod +x /wait-for-it.sh
RUN chmod +x /code/start-server.sh
RUN pip3 install -r requirements.txt
COPY /code/django/ /code/
CMD echo "$pwd"
