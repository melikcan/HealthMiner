import React from "react";
import Joi from "joi-browser";
import Form from "./common/form";
import { getFile, saveFile } from "../services/fileService";

class fileForm extends Form {
  state = {
    data: {
      fileName: "",
      type: "",
      date: ""
    },
    genres: [
      { _id: "Test", name: "Tests" },
      { _id: "Report", name: "Reports" },
      { _id: "Prescription", name: "Prescriptions" },
      { _id: "Insurance", name: "Insurance" }
    ],
    errors: {}
  };

  schema = {
    fileID: Joi.string(),
    fileName: Joi.string()
      .required()
      .label("File Name"),
    type: Joi.string()
      .required()
      .label("Type"),
    date: Joi.string()
      .required()
      .label("Date"),
  };

  async populateFile() {
    try {
      const fileID = this.props.match.params.id;
      if (fileID === "new") return;

      const { data: file } = await getFile(file);
      this.setState({ data: this.mapToViewModel(file) });
    } catch (ex) {
      if (ex.response && ex.response.status === 404)
        this.props.history.replace("/not-found");
    }
  }

  async componentDidMount() {
    await this.populateFile();
  }

  mapToViewModel(file) {
    return {
      fileID: file.fileID,
      fileName: file.fileName,
      type: file.type,
      date: file.date,
    };
  }

  doSubmit = async () => {
    await saveFile(this.state.data);

    this.props.history.push("/files");
  };

  render() {
    return (
      <div>
        <h1>File Form</h1>
        <form onSubmit={this.handleSubmit}>
          {this.renderInput("fileName", "File Name")}
          {this.renderSelect("type", "Type", this.state.genres)}
          {this.renderInput("date", "Date")}
          {this.renderButton("Save")}
        </form>
      </div>
    );
  }
}

export default fileForm;
