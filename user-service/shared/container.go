package shared

import (
	"context"
	"user-service/repository"
	"user-service/service"
)

type Container struct {
	UserService *service.UserService
}

func NewAppContainer(ctx context.Context) *Container {
	userRepo := repository.NewInMemoryUserRepository()
	userService := service.NewUserService(userRepo)
	return &Container{
		UserService: userService,
	}
}
