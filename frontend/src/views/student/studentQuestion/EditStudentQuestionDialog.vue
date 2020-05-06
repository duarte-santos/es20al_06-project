<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog')"
    @keydown.esc="$emit('dialog')"
    max-width="75%"
    max-height="70%"
  >
    <!-- If Cypress is acting funny add height parameter to v-card: <v-card height="500"> -->
    <v-card height="500">
      <v-card-title>
        <span class="headline">
          New Question
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editQuestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editQuestion.title"
                label="Title"
                data-cy="title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editQuestion.content"
                label="Question"
                data-cy="content"
              ></v-textarea>
            </v-flex>
            <v-radio-group v-model="editQuestion.correct">
              <v-flex
                xs24
                sm12
                md12
                v-for="index in editQuestion.options.length"
                :key="index"
              >
                <v-radio
                  class="ma-4"
                  :value="index"
                  label="Correct"
                  :data-cy="`radio${index}`"
                ></v-radio>

                <v-textarea
                  outline
                  rows="10"
                  v-model="editQuestion.options[index - 1]"
                  :label="`Option ${index}`"
                  :data-cy="`option${index - 1}`"
                ></v-textarea>
              </v-flex>
            </v-radio-group>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveQuestion" data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StudentQuestion from '@/models/management/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditStudentQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly question!: StudentQuestion;

  editQuestion!: StudentQuestion;

  created() {
    this.editQuestion = new StudentQuestion(this.question);
  }

  async saveQuestion() {
    if (
      this.editQuestion &&
      (!this.editQuestion.title || !this.editQuestion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    if (this.editQuestion) {
      try {
        const result = await RemoteServices.createStudentQuestion(
          this.editQuestion
        );
        this.$emit('save-studentquestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.v-card {
  padding-top: 5%;
}
</style>
