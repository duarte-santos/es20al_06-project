
export default class TournamentUser {

  id!: number;
  name!: string;
  username!: string;

  constructor(jsonObj?: TournamentUser) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
    }
  }
}