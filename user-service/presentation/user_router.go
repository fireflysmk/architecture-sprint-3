package presentation

import (
	"github.com/gin-gonic/gin"
	"net/http"
	web_schemas "user-service/presentation/web-schemas"
	"user-service/shared"
)

func RegisterUser(c *gin.Context, container *shared.Container) {
	var user web_schemas.NewUserIn

	if err := c.ShouldBindJSON(&user); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request body"})
		return
	}

	err := container.UserService.SignUp(user)
	if err != nil {
		c.JSON(http.StatusConflict, gin.H{"error": "User already exists"})
		return
	}

	c.JSON(http.StatusCreated, gin.H{"message": "User registered successfully"})
}

func GetUserById(c *gin.Context, container *shared.Container) {
	userId := c.Param("userId")

	user, err := container.UserService.GetCurrent(userId)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "user not found"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"user": user})
}

func GetUserByUsername(c *gin.Context, container *shared.Container) {

}

func LoginUser(c *gin.Context, container *shared.Container) {

}

func UpdateUser(c *gin.Context, container *shared.Container) {

}
