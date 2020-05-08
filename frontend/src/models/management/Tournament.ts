import User from '@/models/user/User';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Tournament {
  id!: number;
  title!: string;
  numberOfQuestions!: number;
  startingDate!: string;
  conclusionDate!: string;
  studentList: User[] = [];
  answeredList: User[] = [];
  creatorUsername!: string;

  topicList: Topic[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.creatorUsername = jsonObj.creatorUsername;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.title = jsonObj.title;
      this.startingDate = jsonObj.startingDate;
      this.conclusionDate = jsonObj.conclusionDate;
      this.studentList = jsonObj.studentList;
      this.answeredList = jsonObj.answeredList;

      if (jsonObj.startingDate)
        this.startingDate = ISOtoString(jsonObj.startingDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
    }
  }

  public isEnrolled(user : User){
    for (let i = 0; i<this.studentList.length; i++){
      if (user.username == this.studentList[i].username)
        return true;
    }
    return false;
  }

  public hasAnswered(user : User){
    for (let i = 0; i<this.answeredList.length; i++){
      if (user.username == this.answeredList[i].username)
        return true;
    }
    return false;
  }

}
