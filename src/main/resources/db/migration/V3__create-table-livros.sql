create table livros(
    id bigint not null auto_increment primary key,
    titulo varchar(155),
    autor varchar(155),
    categoria varchar(155),
    estoque integer
)