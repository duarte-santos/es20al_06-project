<template>
  <div class="container">
    <h2>Tournament Dashboard - Tournament results</h2>
    <ul>
      <div>
        <div>
          Privacy={{privacy}}
        </div>
        <v-btn
                medium
                width="3cm"
                color="primary"
                @click="changePrivacy('PUBLIC')"
        >
          MAKE PUBLIC
        </v-btn>
        <v-btn
                medium
                width="3cm"

                color="primary"
                @click="changePrivacy('PRIVATE')"

        >
          MAKE PRIVATE
        </v-btn>
      </div>
      <li class="list-header ">
        <div class="col">Title</div>
        <div class="col">#Questions</div>
        <div class="col">Score</div>
      </li>
      <li class="list-row" v-if="tournaments.length === 0">
        <v-icon size="37" class="img">far fa-sad-tear</v-icon>
        <p class="noT">You have no tournament results</p>
        <v-icon size="37" class="img">far fa-sad-tear</v-icon>
      </li>
      <li
        class="list-row"
        v-for="tournament in tournaments"
        :key="tournament.id"
      >
        <div class="col" data-cy="tournamentTitle">
          {{ tournament.title }}
        </div>
        <div class="col">
          {{ tournament.numberOfQuestions }}
        </div>
        <div class="col">
          {{ calculateScore(tournament) }}
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import StatementManager from '@/models/statement/StatementManager';
import Tournament from '@/models/management/Tournament';
import User from '@/models/user/User';

@Component
export default class TournamentDashboardView extends Vue {
  tournaments: Tournament[] = [];
  quizzes: SolvedQuiz[] = [];
  user: User = this.$store.getters.getUser;
  privacy: String = '';

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.getDashBoardTournaments());
      this.quizzes = (await RemoteServices.getSolvedQuizzes());
      this.privacy = this.user.privacy;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  calculateScore(tournament: Tournament) {
    let correct = 0;
    let quiz;

    for(let i=0; i< this.quizzes.length;i++){
      if(this.quizzes[i].tournamentId == tournament.id){
        quiz = this.quizzes[i];
      }
    }

    if(!quiz)
      return 'N/a'


    for (let i = 0; i < quiz.statementQuiz.questions.length; i++) {
      if (
        quiz.statementQuiz.answers[i] &&
        quiz.correctAnswers[i].correctOptionId ===
          quiz.statementQuiz.answers[i].optionId
      ) {
        correct += 1;
      }
    }
    return `${correct}/${quiz.statementQuiz.questions.length}`;
  }

  async changePrivacy(privacy : string){
    this.privacy = privacy;
    await RemoteServices.changePrivacy(privacy);
  }

}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .noT {
      flex-basis: 50% !important;
      margin: auto; /* Important */
      text-align: center;
      font-size: 25px;
      font-weight: 500;
      opacity: 60%;
    }
    .img {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .col {
      width: 33%;
    }

    .last-col {
      max-width: 50px !important;
    }

    .list-row {
      background-color: #ffffff;
      cursor: pointer;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
    }

  }
}
</style>
