package service

import (
	"errors"
	"user-service/repository"
	"user-service/schemas"
)

type UserService struct {
	repo repository.UserRepository
}

func NewUserService(repo repository.UserRepository) *UserService {
	return &UserService{
		repo: repo,
	}
}

func (s *UserService) SignUp(user schemas.User) error {
	existingUser, _ := s.repo.GetByUsername(user.Username)
	if existingUser.Username != "" {
		return errors.New("user already exists")
	}

	return s.repo.Create(user)
}

func (s *UserService) Login(username, password string) error {
	user, err := s.repo.GetByUsername(username)
	if err != nil || user.Password != password {
		return errors.New("invalid username or password")
	}

	return nil
}

func (s *UserService) Update(user schemas.User) error {
	return s.repo.Update(user)
}

func (s *UserService) GetCurrent(username string) (schemas.User, error) {
	return s.repo.GetByUsername(username)
}
