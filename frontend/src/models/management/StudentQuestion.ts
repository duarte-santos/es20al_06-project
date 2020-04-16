import Image from '@/models/management/Image';

export default class StudentQuestion {
  id: number | null = null;
  title: string = '';
  content: string = '';
  correct: number | null = null;
  image: Image | null = null;
  state: string = 'AWAITING_APPROVAL';
  justification: string = '';

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
      this.options = jsonObj.options;
      this.topics = jsonObj.topics;
    }
  }
}
