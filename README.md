# tiltak-auditlogger
Applikasjon for konsumering av audit-hendelser som oppstår i tiltaksgjennomforing-api og tiltak-refusjon-api, som videresendes til ArcSight for tilsynskontroll.

Eies av Team Tiltak.
<br/>
Kontakt slack: #arbeidsgiver-tiltak

## Testing lokalt

Det er mulig å teste auditlogging lokalt (minus kafka):

1. Sørg for at adressen "audit.nais" peker til localhost. Eksempel `/etc/hosts`-fil:
```
127.0.0.1 audit.nais
```

2. Start en netcat-server som lytter på port 6514:
```sh
sudo nc -v -lp 6514
```

3. Start LokalAuditloggerApplication

4. Send testmeldinger ved å åpne filen `testkall.http` i IntelliJ og kjøre kallet
`POST http://localhost:8080/melding`

Netcat-serveren vil vise auditmeldingen
