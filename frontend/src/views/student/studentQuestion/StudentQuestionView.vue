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
          <v-btn
            color="primary"
            dark
            @click="newQuestion"
            data-cy="createButton"
            >New Question</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content, null)"
          @click="showQuestionDialog(item)"
      /></template>

      <template v-slot:item.topics="{ item }">
        <edit-question-topics
          :question="item"
          :topics="topics"
          v-on:stquestion-changed-topics="onQuestionChangedTopics"
          data-cy="topics"
        />
      </template>

      <template v-slot:item.state="{ item }">
        <v-chip :color="getStateColor(item.state)" dark
          >{{ item.state }}
        </v-chip>
      </template>

      <template v-slot:item.image="{ item }">
        <v-file-input
          show-size
          dense
          small-chips
          @change="handleFileUpload($event, item)"
          accept="image/*"
        />
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showQuestionDialog(item)"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deleteQuestion(item)"
              color="red"
              data-cy="delete"
              >delete</v-icon
            >
          </template>
          <span>Delete Question</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <show-question-dialog
      v-if="currentQuestion"
      v-model="questionDialog"
      :student-question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />

    <edit-question-dialog
      v-if="currentQuestion"
      v-model="editQuestionDialog"
      :question="currentQuestion"
      v-on:save-studentquestion="onSaveStudentQuestion"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import StudentQuestion from '@/models/management/StudentQuestion';
import ShowStudentQuestionDialog from '@/views/student/studentQuestion/ShowStudentQuestionDialog.vue';
import EditStudentQuestionDialog from '@/views/student/studentQuestion/EditStudentQuestionDialog.vue';
import Image from '@/models/management/Image';
import EditStudentQuestionTopics from '@/views/student/studentQuestion/EditStudentQuestionTopics.vue';
import Question from '@/models/management/Question';

@Component({
  components: {
    'show-question-dialog': ShowStudentQuestionDialog,
    'edit-question-dialog': EditStudentQuestionDialog,
    'edit-question-topics': EditStudentQuestionTopics
  }
})
export default class StudentQuestionView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  topics: string[] = [];
  currentQuestion: StudentQuestion | null = null;
  questionDialog: boolean = false;
  editQuestionDialog: boolean = false;
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
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      let auxtopics;
      [auxtopics, this.studentQuestions] = await Promise.all([
        RemoteServices.getTopics(),
        RemoteServices.getStudentQuestions()
      ]);
      for (let i = 0; i < auxtopics.length; i++) {
        this.topics.push(auxtopics[i].name);
      }
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

  newQuestion() {
    this.currentQuestion = new StudentQuestion();
    this.editQuestionDialog = true;
  }

  async onSaveStudentQuestion(question: StudentQuestion) {
    this.studentQuestions = this.studentQuestions.filter(
      q => q.id !== question.id
    );
    this.studentQuestions.unshift(question);
    this.editQuestionDialog = false;
    this.currentQuestion = null;
  }

  onQuestionChangedTopics(questionId: Number, changedTopics: string[]) {
    let question = this.studentQuestions.find(
      (question: StudentQuestion) => question.id == questionId
    );
    if (question) {
      question.topics = changedTopics;
    }
  }

  getStateColor(state: string) {
    if (state === 'REJECTED') return 'red';
    else if (state === 'APPROVED') return 'green';
    else return 'orange';
  }

  async handleFileUpload(event: File, question: Question) {
    if (question.id) {
      try {
        const imageURL = await RemoteServices.updateStudentQuestionImage(
          event,
          question.id
        );
        question.image = new Image();
        question.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async deleteQuestion(question: StudentQuestion) {
    if (
      question.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteStudentQuestion(question.id);
        this.studentQuestions = this.studentQuestions.filter(
          q => q.id != question.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  @Watch('editQuestionDialog')
  closeError() {
    if (!this.editQuestionDialog) {
      this.currentQuestion = null;
    }
  }
}
</script>

<style lang="scss" scoped></style>
