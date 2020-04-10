import TournamentUser from '@/models/user/TournamentUser';
import User from '@/models/user/User';

export default class Tournament {
  id!: number;
  title!: string;
  numberOfQuestions!: number;
  startingDate!: string;
  conclusionDate!: string;
  status!: string;
  enrolled!: boolean;
  studentList: TournamentUser[] = [];
  demoStudentId: number = 676; //for now the only user meant to be used

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.startingDate = jsonObj.startingDate;
      this.conclusionDate = jsonObj.conclusionDate;

      this.updateStudentsList(jsonObj.studentList);
    }
  }

  updateStudentsList(jsonList: any[]) {
    this.studentList = jsonList.map(student => {
      let user = new TournamentUser(student);
      if (user.id === this.demoStudentId) this.enrolled = true;
      return user;
    });
  }
}
