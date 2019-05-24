import React, {Component} from 'react';
import {Link} from 'react-router-dom';
import {toast} from 'react-toastify';
import FilesTable from './filesTable';
import ListGroup from './common/listGroup';
import Pagination from './common/pagination';
import { deleteFile, getUserFiles} from '../services/fileService';
import {paginate} from '../utils/paginate';
import _ from 'lodash';
import SearchBox from './searchBox';
import auth from '../services/authService';

class Home extends Component {
  state = {
    files: [],
    genres: [
      {_id: '', name: 'All Files'},
      {_id: 'Test', name: 'Tests'},
      {_id: 'Report', name: 'Reports'},
      {_id: 'Prescription', name: 'Prescriptions'},
      {_id: 'Insurance', name: 'Insurance'}],
    currentPage: 1,
    pageSize: 4,
    searchQuery: '',
    selectedGenre: null,
    sortColumn: {path: 'fileName', order: 'asc'},
  };

  async componentDidMount() {
    const user = auth.getCurrentUser();
    const {data: files} = await getUserFiles(user.id);
    this.setState({files});
  }

  handleDelete = async file => {
    const originalFiles = this.state.files;
    const files = originalFiles.filter(f => f.fileID !== file.fileID);
    this.setState({files});

    try {
      await deleteFile(file.fileID);
    } catch (ex) {
      if (ex.response && ex.response.status === 404)
        toast.error('This file has already been deleted.');
      this.setState({files: originalFiles});
    }
  };

  handleLike = file => {
    const files = [...this.state.files];
    const index = files.indexOf(file);
    files[index] = {...files[index]};
    files[index].liked = !files[index].liked;
    this.setState({files});
  };

  handlePageChange = page => {
    this.setState({currentPage: page});
  };

  handleGenreSelect = genre => {
    this.setState({selectedGenre: genre, searchQuery: '', currentPage: 1});
  };

  handleSearch = query => {
    this.setState({searchQuery: query, selectedGenre: null, currentPage: 1});
  };

  handleSort = sortColumn => {
    this.setState({sortColumn});
  };

  getPagedData = () => {
    const {
      pageSize,
      currentPage,
      sortColumn,
      selectedGenre,
      searchQuery,
      files: allFiles,
    } = this.state;

    let filtered = allFiles;
    if (searchQuery)
      filtered = allFiles.filter(f =>
          f.fileName.toLowerCase().startsWith(searchQuery.toLowerCase()),
      );
    else if (selectedGenre && selectedGenre._id)
      filtered = allFiles.filter(f => f.type === selectedGenre._id);

    const sorted = _.orderBy(filtered, [sortColumn.path], [sortColumn.order]);

    const files = paginate(sorted, currentPage, pageSize);

    return {totalCount: filtered.length, data: files};
  };

  render() {
    const {length: count} = this.state.files;
    const {pageSize, currentPage, sortColumn, searchQuery} = this.state;
    const {user} = this.props;

    if (count === 0) return <p>There are no files in the database.</p>;

    const {totalCount, data: files} = this.getPagedData();

    return (
        <div className="row">
          <div className="col-3">
            <ListGroup
                items={this.state.genres}
                selectedItem={this.state.selectedGenre}
                onItemSelect={this.handleGenreSelect}
            />
          </div>
          <div className="col">
            {user && <Link
                to="/movies/new"
                className="btn btn-primary"
                style={{marginBottom: 20}}
            >
              New File
            </Link>}
            <p>Showing {totalCount} files in the database.</p>
            <SearchBox value={searchQuery} onChange={this.handleSearch}/>
            <FilesTable
                files={files}
                sortColumn={sortColumn}
                onLike={this.handleLike}
                onDelete={this.handleDelete}
                onSort={this.handleSort}
            />
            <Pagination
                itemsCount={totalCount}
                pageSize={pageSize}
                currentPage={currentPage}
                onPageChange={this.handlePageChange}
            />
          </div>
        </div>
    );
  }
}

export default Home;
