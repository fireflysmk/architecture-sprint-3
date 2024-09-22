package presentation

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"user-service/shared"
)

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

func UpdateUser(c *gin.Context, container *shared.Container) {

}
