package web_schemas

type NewUserIn struct {
	Username string
	Password string
}

type NewUserOut struct {
	ID       string
	Username string
	Password string
}
