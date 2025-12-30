import { useState, useEffect } from 'react';
import { roleService, permissionService } from '../../services/adminService';
import './RolePermissionManagement.css';

const RolePermissionManagement = () => {
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [activeTab, setActiveTab] = useState('roles');
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Form states
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    permissionIds: '',
    resource: '',
    action: ''
  });

  // Pagination
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0
  });

  // Search
  const [searchKeyword, setSearchKeyword] = useState('');

  // Load data
  useEffect(() => {
    loadRoles();
  }, [pagination.page, pagination.size]);

  useEffect(() => {
    if (activeTab === 'permissions') {
      loadPermissions();
    }
  }, [activeTab, pagination.page, pagination.size]);

  const loadRoles = async () => {
    setLoading(true);
    try {
      const response = await roleService.getAllRoles(pagination.page, pagination.size);
      setRoles(response.data.data.content);
      setPagination({
        ...pagination,
        totalElements: response.data.data.totalElements,
        totalPages: response.data.data.totalPages
      });
      setError('');
    } catch (err) {
      setError('Failed to load roles');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const loadPermissions = async () => {
    setLoading(true);
    try {
      const response = await permissionService.getAllPermissions(pagination.page, pagination.size);
      setPermissions(response.data.data.content);
      setPagination({
        ...pagination,
        totalElements: response.data.data.totalElements,
        totalPages: response.data.data.totalPages
      });
      setError('');
    } catch (err) {
      setError('Failed to load permissions');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (activeTab === 'roles') {
        const response = await roleService.searchRoles(searchKeyword, 0, pagination.size);
        setRoles(response.data.data.content);
        setPagination({ ...pagination, page: 0, totalElements: response.data.data.totalElements });
      } else {
        const response = await permissionService.searchPermissions(searchKeyword, 0, pagination.size);
        setPermissions(response.data.data.content);
        setPagination({ ...pagination, page: 0, totalElements: response.data.data.totalElements });
      }
      setError('');
    } catch (err) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({
      name: '',
      description: '',
      permissionIds: '',
      resource: '',
      action: ''
    });
    setShowModal(true);
  };

  const handleEdit = (item) => {
    setEditingId(item.id);
    setFormData(item);
    setShowModal(true);
  };

  const handleSave = async () => {
    setLoading(true);
    try {
      if (activeTab === 'roles') {
        if (editingId) {
          await roleService.updateRole(editingId, formData);
          setSuccess('Role updated successfully');
        } else {
          await roleService.createRole(formData);
          setSuccess('Role created successfully');
        }
        loadRoles();
      } else {
        if (editingId) {
          await permissionService.updatePermission(editingId, formData);
          setSuccess('Permission updated successfully');
        } else {
          await permissionService.createPermission(formData);
          setSuccess('Permission created successfully');
        }
        loadPermissions();
      }
      setShowModal(false);
      setFormData({
        name: '',
        description: '',
        permissionIds: '',
        resource: '',
        action: ''
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this item?')) return;
    
    setLoading(true);
    try {
      if (activeTab === 'roles') {
        await roleService.deleteRole(id);
        setSuccess('Role deleted successfully');
        loadRoles();
      } else {
        await permissionService.deletePermission(id);
        setSuccess('Permission deleted successfully');
        loadPermissions();
      }
    } catch (err) {
      setError('Failed to delete');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleActive = async (id, isActive) => {
    setLoading(true);
    try {
      if (activeTab === 'roles') {
        if (isActive) {
          await roleService.deactivateRole(id);
        } else {
          await roleService.activateRole(id);
        }
        loadRoles();
      } else {
        if (isActive) {
          await permissionService.deactivatePermission(id);
        } else {
          await permissionService.activatePermission(id);
        }
        loadPermissions();
      }
      setSuccess('Status updated successfully');
    } catch (err) {
      setError('Failed to update status');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  return (
    <div className="role-permission-management">
      <div className="container">
        <h1>Role & Permission Management</h1>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="tabs">
          <button
            className={`tab-btn ${activeTab === 'roles' ? 'active' : ''}`}
            onClick={() => { setActiveTab('roles'); setSearchKeyword(''); }}
          >
            Manage Roles
          </button>
          <button
            className={`tab-btn ${activeTab === 'permissions' ? 'active' : ''}`}
            onClick={() => { setActiveTab('permissions'); setSearchKeyword(''); }}
          >
            Manage Permissions
          </button>
        </div>

        <div className="toolbar">
          <form className="search-form" onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="Search..."
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
            />
            <button type="submit">Search</button>
          </form>
          <button className="btn-create" onClick={handleCreate}>
            + Create {activeTab === 'roles' ? 'Role' : 'Permission'}
          </button>
        </div>

        {loading ? (
          <div className="loading">Loading...</div>
        ) : (
          <>
            {activeTab === 'roles' ? (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Description</th>
                      <th>Permissions</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {roles.map((role) => (
                      <tr key={role.id}>
                        <td>{role.id}</td>
                        <td>{role.name}</td>
                        <td>{role.description}</td>
                        <td className="permission-ids">{role.permissionIds || 'None'}</td>
                        <td>
                          <span className={`status ${role.isActive ? 'active' : 'inactive'}`}>
                            {role.isActive ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td className="actions">
                          <button className="btn-edit" onClick={() => handleEdit(role)}>
                            Edit
                          </button>
                          <button className="btn-toggle" onClick={() => handleToggleActive(role.id, role.isActive)}>
                            {role.isActive ? 'Deactivate' : 'Activate'}
                          </button>
                          <button className="btn-delete" onClick={() => handleDelete(role.id)}>
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="table-container">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Description</th>
                      <th>Resource</th>
                      <th>Action</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {permissions.map((permission) => (
                      <tr key={permission.id}>
                        <td>{permission.id}</td>
                        <td>{permission.name}</td>
                        <td>{permission.description}</td>
                        <td>{permission.resource}</td>
                        <td>{permission.action}</td>
                        <td>
                          <span className={`status ${permission.isActive ? 'active' : 'inactive'}`}>
                            {permission.isActive ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td className="actions">
                          <button className="btn-edit" onClick={() => handleEdit(permission)}>
                            Edit
                          </button>
                          <button className="btn-toggle" onClick={() => handleToggleActive(permission.id, permission.isActive)}>
                            {permission.isActive ? 'Deactivate' : 'Activate'}
                          </button>
                          <button className="btn-delete" onClick={() => handleDelete(permission.id)}>
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Pagination */}
            <div className="pagination">
              <button
                disabled={pagination.page === 0}
                onClick={() => setPagination({ ...pagination, page: pagination.page - 1 })}
              >
                Previous
              </button>
              <span>Page {pagination.page + 1} of {pagination.totalPages}</span>
              <button
                disabled={pagination.page >= pagination.totalPages - 1}
                onClick={() => setPagination({ ...pagination, page: pagination.page + 1 })}
              >
                Next
              </button>
            </div>
          </>
        )}

        {/* Modal */}
        {showModal && (
          <div className="modal-overlay" onClick={() => setShowModal(false)}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h2>{editingId ? 'Edit' : 'Create'} {activeTab === 'roles' ? 'Role' : 'Permission'}</h2>
                <button className="close-btn" onClick={() => setShowModal(false)}>×</button>
              </div>
              <div className="modal-body">
                <div className="form-group">
                  <label>Name</label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Description</label>
                  <textarea
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                  />
                </div>
                {activeTab === 'roles' ? (
                  <div className="form-group">
                    <label>Permission IDs (comma-separated)</label>
                    <input
                      type="text"
                      name="permissionIds"
                      value={formData.permissionIds}
                      onChange={handleInputChange}
                      placeholder="e.g., 1,2,3"
                    />
                  </div>
                ) : (
                  <>
                    <div className="form-group">
                      <label>Resource</label>
                      <input
                        type="text"
                        name="resource"
                        value={formData.resource}
                        onChange={handleInputChange}
                      />
                    </div>
                    <div className="form-group">
                      <label>Action</label>
                      <input
                        type="text"
                        name="action"
                        value={formData.action}
                        onChange={handleInputChange}
                      />
                    </div>
                  </>
                )}
              </div>
              <div className="modal-footer">
                <button className="btn-cancel" onClick={() => setShowModal(false)}>Cancel</button>
                <button className="btn-save" onClick={handleSave}>Save</button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RolePermissionManagement;
