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
          v-html="convertMarkDown(item.content, null)"
          @click="showQuestionDialog(item)"
      /></template>

      <template v-slot:item.topics="{ item }">
        <template v-if="item.state === 'APPROVED'">
          <edit-question-topics
            :question="item"
            :topics="topics"
            v-on:stquestion-changed-topics="onQuestionChangedTopics"
            data-cy="topics"
          />
        </template>
      </template>

      <template v-slot:item.image="{ item }">
        <template v-if="item.state === 'APPROVED'">
          <v-file-input
            show-size
            dense
            small-chips
            @change="handleFileUpload($event, item)"
            accept="image/*"
          />
        </template>
      </template>

      <template v-slot:item.state="{ item }">
        <v-chip
          :color="getStateColor(item.state)"
          dark
          @click="evaluateQuestionDialog(item)"
          data-cy="evaluate"
          >{{ item.state }}
        </v-chip>
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
        <v-tooltip bottom v-if="item.state === 'AWAITING_APPROVAL'">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="evaluateQuestionDialog(item)"
              >thumbs_up_down</v-icon
            >
          </template>
          <span>Evaluate Question</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.state === 'APPROVED'">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editQuestion(item)"
              data-cy="available"
              color="blue"
              >check_circle</v-icon
            >
          </template>
          <span>Make Question Available</span>
        </v-tooltip>
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

    <edit-question-dialog
      v-if="currentQuestion"
      v-model="editQuestionDialog"
      :question="currentQuestion"
      v-on:save-studentquestion="makeQuestionAvailable"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StudentQuestion from '@/models/management/StudentQuestion';
import ShowStudentQuestionDialog from '@/views/student/studentQuestion/ShowStudentQuestionDialog.vue';
import EvaluateStudentQuestionDialog from '@/views/teacher/studentQuestions/EvaluateStudentQuestionDialog.vue';
import Image from '@/models/management/Image';
import Question from '@/models/management/Question';
import EditStudentQuestionDialog from '@/views/student/studentQuestion/EditStudentQuestionDialog.vue';
import EditStudentQuestionTopics from '@/views/student/studentQuestion/EditStudentQuestionTopics.vue';

@Component({
  components: {
    'show-question-dialog': ShowStudentQuestionDialog,
    'evaluate-question-dialog': EvaluateStudentQuestionDialog,
    'edit-question-dialog': EditStudentQuestionDialog,
    'edit-question-topics': EditStudentQuestionTopics
  }
})
export default class EvaluateQuestionView extends Vue {
  studentQuestions: StudentQuestion[] = [];
  topics: string[] = [];
  currentQuestion: StudentQuestion | null = null;
  questionDialog: boolean = false;
  evaluateDialog: boolean = false;
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
        RemoteServices.getCourseStudentQuestions()
      ]);
      for (let i = 0; i < auxtopics.length; i++) {
        this.topics.push(auxtopics[i].name);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  showQuestionDialog(question: StudentQuestion) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }

  evaluateQuestionDialog(question: StudentQuestion) {
    if (question.state === 'AWAITING_APPROVAL') {
      this.currentQuestion = question;
      this.evaluateDialog = true;
    }
  }

  async makeQuestionAvailable(question: StudentQuestion) {
    if (question.id) {
      try {
        const result = await RemoteServices.makeStudentQuestionAvailable(
          question.id
        );
        this.studentQuestions = this.studentQuestions.filter(
          q => q.id !== result.id
        );
        this.studentQuestions.unshift(result);
        this.editQuestionDialog = false;
        this.currentQuestion = null;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async onSaveStudentQuestionEvaluation(question: StudentQuestion) {
    this.studentQuestions = this.studentQuestions.filter(
      q => q.id !== question.id
    );
    this.studentQuestions.unshift(question);
    this.evaluateDialog = false;
    this.currentQuestion = null;
  }

  async editQuestion(question: StudentQuestion) {
    this.currentQuestion = question;
    this.editQuestionDialog = true;
  }

  onQuestionChangedTopics(questionId: Number, changedTopics: string[]) {
    let question = this.studentQuestions.find(
      (question: StudentQuestion) => question.id == questionId
    );
    if (question) {
      question.topics = changedTopics;
    }
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

  getStateColor(state: string) {
    if (state === 'REJECTED') return 'red';
    else if (state === 'APPROVED') return 'green';
    else if (state === 'AVAILABLE') return 'blue';
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
