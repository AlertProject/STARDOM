<div class="profile profile-${profile.id}">
    <ul>
        <li class="name"><label>Name</label>${profile.name}</li>
        <li class="lastname"><label>Last Name</label>${profile.lastname}</li>
        <li class="username"><label>Username</label><#if profile.username??>${profile.username}</#if></li>
        <li class="email"><label>E-Mail</label>${profile.email}</li>
    </ul>
</div>