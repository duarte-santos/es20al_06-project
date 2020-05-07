import User from '@/models/user/User';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Tournament {
  id!: number;
  title!: string;
  numberOfQuestions!: number;
  startingDate!: string;
  conclusionDate!: string;
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

      this.studentList = jsonObj.studentList;

      if (jsonObj.startingDate)
        this.startingDate = ISOtoString(jsonObj.startingDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);
    }
  }

  getTitle() {
    return this.title;
  }
}
