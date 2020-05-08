export default class SQDashboard {
  totalQuestions!: number;
  totalApprovedQuestions!: number;
  visible: boolean = true;

  constructor(jsonObj?: SQDashboard) {
    if (jsonObj) {
      this.totalQuestions = jsonObj.totalQuestions;
      this.totalApprovedQuestions = jsonObj.totalApprovedQuestions;
      this.visible = jsonObj.visible;
    }
  }
}
