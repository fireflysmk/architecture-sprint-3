package main

import (
	"context"
	"github.com/gin-gonic/gin"
	"user-service/presentation"
	"user-service/shared"
)

func CreateApp(ctx context.Context) *gin.Engine {
	appContainer := shared.NewAppContainer(ctx)
	r := gin.Default()

	userGroup := r.Group("/users")

	userGroup.GET("/:userId", func(c *gin.Context) { presentation.GetUserById(c, appContainer) })
	userGroup.POST("/register", func(c *gin.Context) { presentation.RegisterUser(c, appContainer) })

	return r
}

func main() {
	ctx := context.Background()
	app := CreateApp(ctx)

	err := app.Run(":8080")
	if err != nil {
		return
	}
}
