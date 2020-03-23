create sequence csl_style_seq;

create table csl_style (
  id integer primary key,
  alias varchar (256) not null,
  site uuid references Site (uuid),
  eperson uuid references EPerson (uuid),
  cslFileName varchar (256) not null,
  cslFile varchar (256) not null,
  constraint site_alias_unique unique (site, alias),
  constraint eperson_alias_unique unique (eperson, alias)
);

create index csl_style_site_index on csl_style (site);
create index csl_style_eperson_index on csl_style (eperson);
create index csl_style_alias_index on csl_style (alias);
