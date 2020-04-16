<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="studentQuestions"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
        </v-card-title>
      </template>

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content, null)"
          @click="showQuestionDialog(item)"
      /></template>
    </v-data-table>

    <show-question-dialog
      v-if="currentQuestion"
      v-model="questionDialog"
      :student-question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/management/StudentQuestion';
import ShowQuestionDialog from '@/views/student/studentQuestion/ShowStudentQuestionDialog.vue';

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog
  }
})
export default class StudentQuestionView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  currentQuestion: StudentQuestion | null = null;
  questionDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Title',
      value: 'title',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Question',
      value: 'content',
      align: 'left',
      width: '25%'
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      sortable: false,
      width: '25%'
    },
    {
      text: 'Image',
      value: 'image',
      align: 'center',
      sortable: false,
      width: '5%'
    },
    { text: 'State', value: 'state', align: 'center', width: '10%' },
    {
      text: 'Justification',
      value: 'justification',
      align: 'center',
      width: '25%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.studentQuestions = await RemoteServices.getStudentQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  showQuestionDialog(question: StudentQuestion) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
