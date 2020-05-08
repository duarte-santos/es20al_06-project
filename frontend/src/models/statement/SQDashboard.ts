export default class SQDashboard {
  totalQuestions!: number;
  totalApprovedQuestions!: number;

  constructor(jsonObj?: SQDashboard) {
    if (jsonObj) {
      this.totalQuestions = jsonObj.totalQuestions;
      this.totalApprovedQuestions = jsonObj.totalApprovedQuestions;
    }
  }
}
