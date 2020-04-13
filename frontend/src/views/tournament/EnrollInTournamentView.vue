<template>
  <div class="container">
    <h2>Available Tournaments</h2>
    <ul data-cy="tournamentsList">
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Questions</div>
        <div class="col">Beginning</div>
        <div class="col">End</div>
        <div class="col last-col">Enrolled symbol</div>
      </li>
      <li
        class="list-row"
        v-for="tournament in tournaments"
        :key="tournament.id"
        @click="enroll(tournament)"
      >
        <div class="col" style="font-weight: bold">
          {{ tournament.title }}
        </div>
        <div class="col">
          {{ tournament.numberOfQuestions }}
        </div>
        <div class="col">
          {{ tournament.startingDate }}
        </div>
        <div class="col">
          {{ tournament.conclusionDate }}
        </div>
        <div class="col last-col">
          <div v-if="tournament.enrolled">
            <v-icon style="color: green" data-cy="enrollmentTick">check</v-icon>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Tournament from '@/models/management/Tournament';


@Component
export default class EnrollInTournamentView extends Vue {
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getAvailableTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async enroll(tournament: Tournament) {
    /*if(tournament.enrolled) {
      alert('you were already enrolled in the ' + tournament.title);
      return;
    }
*/
    await this.$store.dispatch('loading');
    try {
      tournament.updateStudentsList(await RemoteServices.enrollInTournament(tournament));
      this.$forceUpdate();
      alert('you are now enrolled in the ' + tournament.title);

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
