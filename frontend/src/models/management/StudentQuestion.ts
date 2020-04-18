import Image from '@/models/management/Image';
import { Student } from '@/models/management/Student';

export default class StudentQuestion {
  id: number | null = null;
  title: string = '';
  content: string = '';
  correct: number | null = null;
  image: Image | null = null;
  state: string = 'AWAITING_APPROVAL';
  justification: string = '';
  student: Student | null = null;

  options: string[] = ['', '', '', ''];
  topics: string[] = [];

  constructor(jsonObj?: StudentQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.correct = jsonObj.correct;
      this.image = jsonObj.image;
      this.state = jsonObj.state;
      this.justification = jsonObj.justification;
      this.student = jsonObj.student;
      this.options = jsonObj.options;
      this.topics = jsonObj.topics;
    }
  }
}
