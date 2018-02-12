create table game
(
    gameNumber int primary key,
    winner varchar(50),
    drawCount int,
	totalround int
)
create table winRound
(
    player_name varchar(50),
    win_round_count int,
    gameNumber int CONSTRAINT fk_game REFERENCES game(gameNumber),
    CONSTRAINT pkey PRIMARY KEY (player_name,gameNumber)
)