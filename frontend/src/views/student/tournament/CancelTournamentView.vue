<template>
  <div class="container">
    <h2>All Tournaments</h2>
    <ul data-cy="tournamentsList">
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Questions</div>
        <div class="col">Start</div>
        <div class="col">End</div>
        <div class="col">Creator</div>
        <div class="col">Status</div>

        <div class="col last-col"><v-icon>fas fa-check</v-icon></div>
      </li>
      <li class="list-row" v-if="tournaments.length == 0">
        <v-icon size="37" class="img">far fa-sad-tear</v-icon>
        <p class="noT">There are no available tournaments</p>
        <v-icon size="37" class="img">far fa-sad-tear</v-icon>
      </li>
      <li
        class="list-row"
        v-for="(tournament,index) in tournaments"
        :key="tournament.id"
        :id="tournament.id"
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
        <div class="col">
          {{ tournament.creatorUsername }}
        </div>
        <div class="col">
          {{ status[index] }}
        </div>
        <div class="col last-col">
          <div>
            <v-icon v-if="status[index] == 'CLOSED' && tournament.creatorUsername == user.username"
                    style="color: darkred"
                    @click="cancel(tournament)"
            >
              fas fa-trash
            </v-icon>
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
import User from '@/models/user/User';

@Component
export default class CancelTournamentView extends Vue {
  tournaments: Tournament[] = [];
  user: User = new User();
  status: String[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.user = this.$store.getters.getUser;
      this.tournaments = await RemoteServices.getAvailableTournaments();

      for(let i = 0; i < this.tournaments.length; i++){
        let start = new Date(this.tournaments[i].startingDate);
        let current = new Date()
        let end = new Date(this.tournaments[i].conclusionDate);
        if (current.getTime() > start.getTime() && current.getTime() < end.getTime()){
          this.status[i] = new String('OPEN');
        }
        else{
          this.status[i] = new String('CLOSED');
        }

      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async cancel(tournament: Tournament) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.deleteTournament(tournament.id);

      document.getElementById(tournament.id.toString())!.style.visibility = 'hidden';
      this.$forceUpdate();
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

    .col last-col {
      width: 13%;
    }

    .col {
      width: 14% !important;
      margin: auto; /* Important */
      text-align: center;
      word-break: break-word;
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
