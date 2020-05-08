<template>
  <div class="container">
    <v-container v-if="!submitDialog" class="create-form">
      <v-card-title>
        <span>Create Tournament</span>
      </v-card-title>
      <v-card-text>
        <v-text-field
          v-model="tournament.title"
          label="Title"
          data-cy="title"
        />

        <v-row>
          <v-col cols="12" sm="6">
            <VueCtkDateTimePicker
              label="Starting Date"
              id="startingDateInput"
              v-model="tournament.startingDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
              data-cy="startingDate"
            ></VueCtkDateTimePicker>
          </v-col>
          <v-col cols="12" sm="6">
            <VueCtkDateTimePicker
              label="Conclusion Date"
              id="conclusionDateInput"
              v-model="tournament.conclusionDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
              data-cy="conclusionDate"
            ></VueCtkDateTimePicker>
          </v-col>
          <v-spacer></v-spacer>
        </v-row>

        <v-container>
          <p class="pl-0">Number of Questions</p>
          <v-btn-toggle
            v-model="tournament.numberOfQuestions"
            mandatory
            class="button-group"
          >
            <v-btn text value="5" data-cy="5questions">5</v-btn>
            <v-btn text value="10" data-cy="10questions">10</v-btn>
            <v-btn text value="15" data-cy="15questions">15</v-btn>
            <v-btn text value="20" data-cy="20questions">20</v-btn>
            <v-btn text value="25" data-cy="25questions">25</v-btn>
            <v-btn text value="30" data-cy="30questions">30</v-btn>
          </v-btn-toggle>
        </v-container>

        <v-container>
          <v-card>
            <v-card-title>
              Choose Topics
              <v-spacer></v-spacer>
              <v-text-field
                v-model="searchTopics"
                append-icon="mdi-magnify"
                label="Search"
                single-line
                hide-details
              ></v-text-field>
            </v-card-title>
            <v-spacer></v-spacer>
            <v-data-table
              :headers="TopicHeaders"
              :items="allTopics"
              :mobile-breakpoint="0"
              :search="searchTopics"
              class="elevation-1"
              item-key="name"
              selected="selectedTopics"
              data-cy="topics"
            >
              <template v-slot:item.opt="{ item }" data-cy="yo">
                <v-checkbox
                  class="checkboxin"
                  @change="store(item)"
                ></v-checkbox>
              </template>
            </v-data-table>
          </v-card>
        </v-container>

        <v-container>
          <v-btn color="green" width="7cm" @click="submit" data-cy="submit">
            SUBMIT
          </v-btn>
        </v-container>
      </v-card-text>
    </v-container>

    <v-container v-if="submitDialog" class="create-form">
      <v-card-title>
        <span id="success">Tournament Submitted!</span>
      </v-card-title>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import Assessment from '@/models/management/Assessment';
import RemoteServices from '@/services/RemoteServices';
import Topic from '@/models/management/Topic';
import Tournament from '@/models/management/Tournament';
import { Quiz } from '@/models/management/Quiz';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component
export default class CreateTournamentView extends Vue {
  tournament: Tournament = new Tournament();
  allTopics: Topic[] = [];
  searchTopics: string = '';
  submitDialog: boolean = false;

  TopicHeaders: object = [
    {
      text: '',
      value: 'opt',
      align: 'center',
      width: '150px',
      sortable: false
    },
    {
      text: 'Topic',
      value: 'name',
      align: 'center',
      width: '150px',
      sortable: true
    },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '150px',
      sortable: true
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.allTopics] = await Promise.all([RemoteServices.getTopics()]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async submit() {
    if (await this.assertInput()) {
      return;
    }

    try {
      let a = await RemoteServices.createTournament(this.tournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
      return;
    }

    this.submitDialog = true;
  }

  async assertInput() {
    if (!this.tournament.title) {
      await this.$store.dispatch(
        'error',
        'Error: The tournament must have a title'
      );
      return 1;
    }
    if (!this.tournament.startingDate) {
      await this.$store.dispatch(
        'error',
        'Error: The tournament must have a starting date'
      );
      return 1;
    }
    if (!this.tournament.conclusionDate) {
      await this.$store.dispatch(
        'error',
        'Error: The tournament must have a conclusion date'
      );
      return 1;
    }
    if (this.tournament.topicList.length == 0) {
      await this.$store.dispatch(
        'error',
        'Error: You must select at least 1 topic'
      );
      return 1;
    }
    return 0;
  }

  async store(item: Topic) {
    if (this.tournament.topicList.includes(item)) {
      let index = this.tournament.topicList.indexOf(item);
      if (index !== -1) this.tournament.topicList.splice(index, 1);
      return;
    }
    this.tournament.topicList.push(item);
    return;
  }
}
</script>

<style lang="scss" scoped>
.create-form {
  width: 80% !important;
  background-color: white;
  border-width: 10px;
  border-style: solid;
  border-color: #818181;
}

.button-group {
  flex-wrap: wrap;
  justify-content: center;
}

.pl-0 {
  font-size: 18px;
}

#success {
  color: #299455;
  margin: auto;
  font-size: 25px;
}

.checkboxin {
  margin-left: 20px;
}
</style>
