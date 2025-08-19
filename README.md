# Digital Insurance Management System

# digital-insurance

To run the services, you need to have Docker installed on your machine. Follow these steps:
Use
```
docker-compose --profile migrations up --build
```
to start the application and run the migrations.
This command will build the Docker images and start the services defined in the `docker-compose.yml` file, including the database and the application server.

To access the PostgreSQL database, you can use the following command:
```
docker exec -it insurance-postgres psql -U postgres -d insurance_db 
```

TO stop the services, you can use:
```
docker-compose down
```

To stop the services and remove the volumes, you can use:
```
docker-compose down -v --remove-orphans
```