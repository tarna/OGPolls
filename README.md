# OGPolls
OGPolls is a Minecraft plugin that allows staff members to create and manage in game polls.

## Commands
| Command                             | Permission                   | Description                                                          |
|-------------------------------------|------------------------------|----------------------------------------------------------------------|
| /polls                              | ogpolls.command.polls        | View a list of active polls.                                         |
| /polls create (duration) (question) | ogpolls.command.polls.create | Starts a poll creation process with a certain duration and question. |
| /polls close (id)                   | ogpolls.command.polls.close  | Closes a poll with a certain ID.                                     |
| /polls delete (id)                  | ogpolls.command.polls.delete | Deletes a poll with a certain id                                     |

# Config
```yml
mongo:
  # The mongoDB URI to connect to.
  uri: "mongodb://localhost:27017"
  # The database name to use for storing poll data.
  database: "ogpolls"
```