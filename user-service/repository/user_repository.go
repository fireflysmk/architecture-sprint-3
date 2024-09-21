package repository

import (
	"errors"
	"user-service/schemas"
)

type UserRepository interface {
	Create(user schemas.User) error
	GetByUsername(username string) (schemas.User, error)
	Update(user schemas.User) error
}

type InMemoryUserRepository struct {
	users map[string]schemas.User
}

func NewInMemoryUserRepository() *InMemoryUserRepository {
	repo := &InMemoryUserRepository{
		users: make(map[string]schemas.User),
	}

	// debug user storage
	repo.users["123"] = schemas.User{
		ID:       "123",
		Username: "john_doe",
		Password: "password123",
		Email:    "john@example.com",
	}

	repo.users["321"] = schemas.User{
		ID:       "321",
		Username: "vlad_baidin",
		Password: "some_pswd123",
		Email:    "vlad_baidin@example.com",
	}

	return repo
}

func (r *InMemoryUserRepository) Create(user schemas.User) error {
	if _, exists := r.users[user.Username]; exists {
		return errors.New("user already exists")
	}
	r.users[user.Username] = user
	return nil
}

func (r *InMemoryUserRepository) GetByUsername(username string) (schemas.User, error) {
	user, exists := r.users[username]
	if !exists {
		return schemas.User{}, errors.New("user not found")
	}
	return user, nil
}

func (r *InMemoryUserRepository) Update(user schemas.User) error {
	if _, exists := r.users[user.Username]; !exists {
		return errors.New("user not found")
	}
	r.users[user.Username] = user
	return nil
}
