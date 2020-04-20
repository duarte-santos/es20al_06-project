<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="75%"
    max-height="70%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          Evaluate Question
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="evaluateQuestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md12>
              <p><b>Title:</b> {{ question.title }}</p>
              <p><b>Question:</b> {{ question.content }}</p>
              <ul>
                <li v-for="(option, index) in question.options" :key="index">
                  <span
                    v-if="question.correct - 1 === index"
                    v-html="convertMarkDownNoFigure('**[★]** ', null)"
                  />
                  <span
                    v-html="convertMarkDownNoFigure(option, null)"
                    v-bind:class="[
                      question.correct - 1 === index ? 'font-weight-bold' : ''
                    ]"
                  />
                </li>
              </ul>
            </v-flex>
            <p></p>
            <v-divider></v-divider>
            <v-flex xs16 sm8 md8>
              <v-radio-group v-model="evaluateQuestion.state">
                <v-radio
                  class="ma-4"
                  :value="`APPROVED`"
                  :label="`Approved`"
                  color="success"
                ></v-radio>
                <v-radio
                  class="ma-4"
                  :value="`REJECTED`"
                  :label="`Rejected`"
                  color="error"
                ></v-radio>
              </v-radio-group>
              <v-divider></v-divider>
              <v-textarea
                outline
                rows="10"
                v-model="evaluateQuestion.justification"
                label="Justification"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveQuestion">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StudentQuestion from '@/models/management/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';

@Component
export default class EvaluateStudentQuestionDialog extends Vue {
  @Model('close-dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly question!: StudentQuestion;

  evaluateQuestion!: StudentQuestion;

  created() {
    this.evaluateQuestion = new StudentQuestion();
    this.evaluateQuestion.id = this.question.id;
    console.log(this.evaluateQuestion.id);
  }

  async saveQuestion() {
    if (
      this.evaluateQuestion &&
      this.evaluateQuestion.state == 'REJECTED' &&
      !this.evaluateQuestion.justification
    ) {
      await this.$store.dispatch(
        'error',
        'Rejected question must have justification'
      );
      return;
    }

    if (this.evaluateQuestion && this.question.id != null) {
      try {
        const result = await RemoteServices.evaluateStudentQuestion(
          this.evaluateQuestion
        );
        this.$emit('save-studentquestionevaluation', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }
}
</script>
