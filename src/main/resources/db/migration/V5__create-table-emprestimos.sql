create table emprestimos (
    id bigint auto_increment not null primary key,
    inicio date,
    fim date,
    multa double,
    livro_id bigint,
    status varchar(100),
    usuario_id bigint
)