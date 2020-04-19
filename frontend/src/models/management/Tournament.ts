import User from '@/models/user/User';
import Topic from '@/models/management/Topic';

export default class Tournament {
  id!: number;
  title!: string;
  numberOfQuestions!: number;
  startingDate!: string | undefined;
  conclusionDate!: string | undefined;
  status!: string | undefined;
  studentList: User[] = [];


  topicList: Topic[] = [];

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
    if (jsonList) {
      this.studentList = jsonList.map(
          (user: User) => new User(user)
      );
    }
  }

  checkStudent(user :User) {
    for (let student of this.studentList) {
      if (student.name == user.name){
        return true;
      }
    }
    return false;
  }

}
