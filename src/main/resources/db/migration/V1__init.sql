create table audit_entries (
    id serial primary key,
    utfoert_av text NOT NULL,
    oppslag_paa_fnr text NOT NULL,
    oppslag_paa_entitet text NOT NULL,
    sesjon_id text NOT NULL,
    oppslag_tidspunkt timestamptz NOT NULL,
    utfoert_arbeidsgiver_fnr text
);