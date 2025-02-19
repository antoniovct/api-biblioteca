create table reservas (
    id bigint auto_increment primary key not null,
    data datetime,
    inicio datetime,
    expiracao datetime,
    usuario_id bigint,
    livro_id bigint,
    status varchar(100)
)