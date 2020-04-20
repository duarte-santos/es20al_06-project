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
        </v-card-title>
      </template>

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content, null)"
          @click="showQuestionDialog(item)"
      /></template>

      <template v-slot:item.state="{ item }">
        <v-chip
          :color="getStateColor(item.state)"
          dark
          @click="evaluateQuestionDialog(item)"
          >{{ item.state }}
        </v-chip>
      </template>
    </v-data-table>

    <show-question-dialog
      v-if="currentQuestion"
      v-model="questionDialog"
      :student-question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />

    <evaluate-question-dialog
      v-if="currentQuestion"
      v-model="evaluateDialog"
      :question="currentQuestion"
      v-on:save-studentquestionevaluation="onSaveStudentQuestionEvaluation"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import StudentQuestion from '@/models/management/StudentQuestion';
import ShowStudentQuestionDialog from '@/views/student/studentQuestion/ShowStudentQuestionDialog.vue';
import EvaluateStudentQuestionDialog from '@/views/teacher/studentQuestions/EvaluateStudentQuestionDialog.vue';
import Image from '@/models/management/Image';
import Question from '@/models/management/Question';

@Component({
  components: {
    'show-question-dialog': ShowStudentQuestionDialog,
    'evaluate-question-dialog': EvaluateStudentQuestionDialog
  }
})
export default class EvaluateQuestionView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  currentQuestion: StudentQuestion | null = null;
  questionDialog: boolean = false;
  evaluateDialog: boolean = false;
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
      width: '20%'
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      sortable: false,
      width: '20%'
    },
    {
      text: 'Image',
      value: 'image',
      align: 'center',
      sortable: false,
      width: '5%'
    },
    {
      text: 'Student',
      value: 'student.name',
      align: 'center',
      width: '10%'
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
      this.studentQuestions = await RemoteServices.getCourseStudentQuestions();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  showQuestionDialog(question: StudentQuestion) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }

  evaluateQuestionDialog(question: StudentQuestion) {
    this.currentQuestion = question;
    this.evaluateDialog = true;
  }

  async onSaveStudentQuestionEvaluation(
    question: StudentQuestion
  ) {
    this.studentQuestions = this.studentQuestions.filter(q => q.id !== question.id);
    this.studentQuestions.unshift(question);
    this.evaluateDialog = false;
    this.currentQuestion = null;
  }

  getStateColor(state: string) {
    if (state === 'REJECTED') return 'red';
    else if (state === 'APPROVED') return 'green';
    else return 'orange';
  }

  @Watch('evaluateDialog')
  closeError() {
    if (!this.evaluateDialog) {
      this.currentQuestion = null;
    }
  }
}
</script>

<style lang="scss" scoped></style>
