import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import { ISOtoString } from '@/services/ConvertDateService';

export default class SolvedQuiz {
  answerDate!: string;
  statementQuiz!: StatementQuiz;
  correctAnswers: StatementCorrectAnswer[] = [];
  tournamentId!: number;

  constructor(jsonObj?: SolvedQuiz) {
    if (jsonObj) {
      this.answerDate = ISOtoString(jsonObj.answerDate);
      this.statementQuiz = new StatementQuiz(jsonObj.statementQuiz);
      this.tournamentId = jsonObj.tournamentId;

      this.correctAnswers = jsonObj.correctAnswers.map(correctAnswer => {
        return new StatementCorrectAnswer(correctAnswer);
      });
    }
  }
}
