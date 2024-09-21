package shared

import (
	"context"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"log"
	"user-service/persistance"
	"user-service/repository"
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

	err = db.AutoMigrate(&persistance.UserModel{})
	if err != nil {
		return nil
	}

	userRepo := repository.NewGORMUserRepository(db)
	userService := service.NewUserService(userRepo)
	return &Container{
		UserService: userService,
	}
}
