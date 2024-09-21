package shared

import (
	"context"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"log"
	"user-service/repository"
	dto_schemas "user-service/repository/dto-schemas"
	"user-service/service"
)

type Container struct {
	UserService *service.UserService
}

func NewAppContainer(ctx context.Context) *Container {
	dsn := "host=0.0.0.0 user=postgres password=postgres dbname=sprint_3 port=5432 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatalf("failed to connect to database: %v", err)
	}

	err = db.AutoMigrate(&dto_schemas.UserDtoSchema{})
	if err != nil {
		return nil
	}

	userRepo := repository.NewGORMUserRepository(db)
	userService := service.NewUserService(userRepo)
	return &Container{
		UserService: userService,
	}
}
