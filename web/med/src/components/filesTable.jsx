import React, {Component} from 'react';
import {Link} from 'react-router-dom';
import auth from '../services/authService';
import Table from './common/table';

class FilesTable extends Component {
  columns = [
    {
      path: 'fileName',
      label: 'Title',
      content: file => <Link key={file.fileID} to={`/home/${file.fileID}`}>{file.fileName}</Link>,
    },
    {path: 'type', label: 'Type'},
    {path: 'date', label: 'Date'}

  ];

  deleteColumn = {
    key: 'delete',
    content: file => (
        <button
            onClick={() => this.props.onDelete(file)}
            className="btn btn-danger btn-sm"
        >
          Delete
        </button>
    ),
  };

  constructor() {
    super();
    const user = auth.getCurrentUser();
    if (user && user.isAdmin)
      this.columns.push(this.deleteColumn);
  }

  render() {
    const {files, onSort, sortColumn} = this.props;

    return (
        <Table
            columns={this.columns}
            data={files}
            sortColumn={sortColumn}
            onSort={onSort}
        />
    );
  }
}

export default FilesTable;
