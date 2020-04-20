<template>
  <v-form>
    <v-autocomplete
      v-model="questionTopics"
      :items="topics"
      multiple
      return-object
      @change="saveTopics"
    >
      <template v-slot:selection="data">
        <v-chip
          v-bind="data.attrs"
          :input-value="data.selected"
          close
          @click="data.select"
          @click:close="removeTopic(data.item)"
        >
          {{ data.item }}
        </v-chip>
      </template>
      <template v-slot:item="data">
        <v-list-item-content>
          <v-list-item-title v-html="data.item" />
        </v-list-item-content>
      </template>
    </v-autocomplete>
  </v-form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import StudentQuestion from '@/models/management/StudentQuestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditStudentQuestionTopics extends Vue {
  @Prop({ type: StudentQuestion, required: true })
  readonly question!: StudentQuestion;
  @Prop({ type: Array, required: true }) readonly topics!: string[];

  questionTopics: string[] = this.question.topics;

  async saveTopics() {
    if (this.question.id) {
      try {
        await RemoteServices.updateStudentQuestionTopics(
          this.question.id,
          this.questionTopics
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    this.$emit(
      'stquestion-changed-topics',
      this.question.id,
      this.questionTopics
    );
  }

  removeTopic(topic: string) {
    this.questionTopics = this.questionTopics.filter(
      element => element != topic
    );
    this.saveTopics();
  }
}
</script>
