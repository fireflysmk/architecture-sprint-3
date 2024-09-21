package persistance

import "gorm.io/gorm"

type UserModel struct {
	gorm.Model
	Username string `gorm:"uniqueIndex;size:100"`
	Password string `gorm:"size:255"`
}
