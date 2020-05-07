<template>
  <div class="container">
    <h2>Open Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">End</div>
        <div class="col">#Questions</div>
        <div class="col">Answer Quiz</div>
      </li>
      <li class="list-row" v-if="tournaments.length == 0">
        <v-icon size="37" class="img">far fa-sad-tear</v-icon>
        <p class="noT">There are no open tournaments</p>
        <v-icon size="37" class="img">far fa-sad-tear</v-icon>
      </li>
      <li
        class="list-row"
        v-for="tournament in tournaments"
        :key="tournament.id"
      >
        <div data-cy="tournamentTitle" class="col">
          {{ tournament.title }}
        </div>
        <div class="col">
          {{ tournament.conclusionDate }}
        </div>
        <div class="col">
          {{ tournament.numberOfQuestions }}
        </div>
        <div :id="tournament.id" class="col last-col">
          <v-btn medium
            width="3cm"
            color="primary"
            @click="answerQuiz(tournament)"
            v-if="tournament.studentList.length > 1"
            :data-cy="tournament.title + '.startButton'"
          >
            START
          </v-btn>
          <v-icon v-else> </v-icon>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import Tournament from '@/models/management/Tournament';

@Component
export default class OpenTournamentsView extends Vue {
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.getOpenTournaments()).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async answerQuiz(tournament: Tournament) {
    await this.$store.dispatch('loading');
    try {
      document.getElementById(tournament.id.toString())!.style.visibility =
        'hidden';
      // TODO call remote services
      // this.$forceUpdate();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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

    .col {
      flex-basis: 33% !important;
      margin: auto; /* Important */
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

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
