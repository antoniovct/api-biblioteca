create table usuarios
(
    id bigint auto_increment not null primary key ,
    nome varchar(155),
    email varchar(155),
    senha varchar(155),
    cpf char(11)
)